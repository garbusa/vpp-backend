package de.uol.vpp.load.infrastructure.scheduler;

import de.uol.vpp.load.domain.aggregates.LoadAggregate;
import de.uol.vpp.load.domain.entities.LoadHouseholdEntity;
import de.uol.vpp.load.domain.exceptions.LoadException;
import de.uol.vpp.load.domain.exceptions.LoadHouseholdRepositoryException;
import de.uol.vpp.load.domain.exceptions.LoadRepositoryException;
import de.uol.vpp.load.domain.repositories.ILoadHouseholdRepository;
import de.uol.vpp.load.domain.repositories.ILoadRepository;
import de.uol.vpp.load.domain.valueobjects.*;
import de.uol.vpp.load.infrastructure.rabbitmq.RabbitMQSender;
import de.uol.vpp.load.infrastructure.rest.MasterdataRestClient;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class LoadScheduler {

    private static final int FORECAST_PERIODS = 24 * 4; //24h in 15minutes;

    private final MasterdataRestClient masterdataRestClient;
    private final ILoadRepository loadRepository;
    private final ILoadHouseholdRepository loadHouseholdRepository;
    private final RabbitMQSender rabbitMQSender;
    private HSSFSheet sheet;
    private Map<Integer, Integer> timeRowMap;

    public LoadScheduler(MasterdataRestClient masterdataRestClient,
                         ILoadRepository loadRepository,
                         ILoadHouseholdRepository loadHouseholdRepository,
                         RabbitMQSender rabbitMQSender) {
        this.masterdataRestClient = masterdataRestClient;
        this.loadRepository = loadRepository;
        this.loadHouseholdRepository = loadHouseholdRepository;
        this.rabbitMQSender = rabbitMQSender;
        try {
            File file = ResourceUtils.getFile("classpath:slp.xls");
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
            sheet = workbook.getSheetAt(0);
            timeRowMap = createTimeRowMap();
        } catch (IOException e) {
            log.error("failed to load excel file");
        }
    }

    private Map<Integer, Integer> createTimeRowMap() {
        return new HashMap<>() {
            {
                put(15, 3);
                put(30, 4);
                put(45, 5);
                put(100, 6);
                put(115, 7);
                put(130, 8);
                put(145, 9);
                put(200, 10);
                put(215, 11);
                put(230, 12);
                put(245, 13);
                put(300, 14);
                put(315, 15);
                put(330, 16);
                put(345, 17);
                put(400, 18);
                put(415, 19);
                put(430, 20);
                put(445, 21);
                put(500, 22);
                put(515, 23);
                put(530, 24);
                put(545, 25);
                put(600, 26);
                put(615, 27);
                put(630, 28);
                put(645, 29);
                put(700, 30);
                put(715, 31);
                put(730, 32);
                put(745, 33);
                put(800, 34);
                put(815, 35);
                put(830, 36);
                put(845, 37);
                put(900, 38);
                put(915, 39);
                put(930, 40);
                put(945, 41);
                put(1000, 42);
                put(1015, 43);
                put(1030, 44);
                put(1045, 45);
                put(1100, 46);
                put(1115, 47);
                put(1130, 48);
                put(1145, 49);
                put(1200, 50);
                put(1215, 51);
                put(1230, 52);
                put(1245, 53);
                put(1300, 54);
                put(1315, 55);
                put(1330, 56);
                put(1345, 57);
                put(1400, 58);
                put(1415, 59);
                put(1430, 60);
                put(1445, 61);
                put(1500, 62);
                put(1515, 63);
                put(1530, 64);
                put(1545, 65);
                put(1600, 66);
                put(1615, 67);
                put(1630, 68);
                put(1645, 69);
                put(1700, 70);
                put(1715, 71);
                put(1730, 72);
                put(1745, 73);
                put(1800, 74);
                put(1815, 75);
                put(1830, 76);
                put(1845, 77);
                put(1900, 78);
                put(1915, 79);
                put(1930, 80);
                put(1945, 81);
                put(2000, 82);
                put(2015, 83);
                put(2030, 84);
                put(2045, 85);
                put(2100, 86);
                put(2115, 87);
                put(2130, 88);
                put(2145, 89);
                put(2200, 90);
                put(2215, 91);
                put(2230, 92);
                put(2245, 93);
                put(2300, 94);
                put(2315, 95);
                put(2330, 96);
                put(2345, 97);
                put(0, 98);
            }
        };
    }


    @Scheduled(cron = "0 0/15 * 1/1 * *")
    public void createLoad() {
        List<String> ids = masterdataRestClient.getAllActiveVppIds();
        ZonedDateTime currentZDT = ZonedDateTime.now(ZoneId.of("GMT+2"));

        List<Thread> threads = new ArrayList<>();
        ids.forEach(id -> threads.add(new Thread(() -> {
            ZonedDateTime threadCurrentZDT = ZonedDateTime.of(
                    currentZDT.getYear(), currentZDT.getMonthValue(), currentZDT.getDayOfMonth(), currentZDT.getHour(), currentZDT.getMinute(),
                    0, 0, ZoneId.of("GMT+2")
            );
            this.outdateLoads(id, threadCurrentZDT);
            this.saveLoad(threadCurrentZDT, id, false);
            for (int forecastIndex = 0; forecastIndex < LoadScheduler.FORECAST_PERIODS; forecastIndex++) {
                threadCurrentZDT = threadCurrentZDT.plusMinutes(15L);
                this.saveLoad(threadCurrentZDT, id, true);
            }

        }, id)));

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.info("failed to wait for thread {}", thread.getName());
            }
        }

        rabbitMQSender.send("loads generated successfully");


    }

    private void outdateLoads(String id, ZonedDateTime currentZDT) {
        try {
            loadRepository.outdateLoad(new LoadVirtualPowerPlantIdVO(id), currentZDT);
        } catch (LoadRepositoryException | LoadException e) {
            log.info("outdate loads failed");
        }
    }

    private void saveLoad(ZonedDateTime currentZDT, String id, boolean forecast) {
        try {
            int rowIndex = getRowIndex(currentZDT);
            int columnIndex = getColumnIndex(currentZDT);
            LoadAggregate loadAggregate = new LoadAggregate();
            loadAggregate.setLoadVirtualPowerPlantId(new LoadVirtualPowerPlantIdVO(id));
            loadAggregate.setLoadStartTimestamp(new LoadStartTimestampVO(currentZDT.toEpochSecond()));
            loadAggregate.setLoadIsForecasted(new LoadIsForecastedVO(forecast));
            loadAggregate.setLoadIsOutdated(new LoadIsOutdatedVO(false));
            loadRepository.saveLoad(loadAggregate);
            List<String> householdIds = masterdataRestClient.getAllHouseholdsByVppId(id);
            householdIds.forEach(householdId -> {
                try {
                    LoadHouseholdEntity householdEntity = new LoadHouseholdEntity();
                    householdEntity.setLoadHouseholdStartTimestamp(new LoadHouseholdStartTimestampVO(currentZDT.toEpochSecond()));
                    householdEntity.setLoadHouseholdId(new LoadHouseholdIdVO(householdId));
                    householdEntity.setLoadHouseholdMemberAmount(
                            new LoadHouseholdMemberAmountVO(masterdataRestClient.getHouseholdMemberAmountById(householdId))
                    );
                    householdEntity.setLoadHouseholdValueVO(
                            new LoadHouseholdValueVO(
                                    sheet.getRow(rowIndex).getCell(columnIndex).getNumericCellValue() * householdEntity.getLoadHouseholdMemberAmount().getAmount()
                            )
                    );
                    loadHouseholdRepository.saveLoadHousehold(householdEntity);
                    loadHouseholdRepository.assign(householdEntity, loadAggregate);
                    log.info("household saved with value {}", householdEntity.getLoadHouseholdValueVO().getValue());
                } catch (LoadException | LoadHouseholdRepositoryException e) {
                    log.info("lol");
                }
            });
        } catch (LoadException | LoadRepositoryException e) {
            log.info("lol");
        }
    }

    private int getRowIndex(ZonedDateTime date) {
        int key = (date.getHour() * 100) + date.getMinute();
        log.info("current key for searching index: {}", key);
        return timeRowMap.get(key);
    }

    private int getColumnIndex(ZonedDateTime date) {
        String season = this.getSeason(date);
        switch (season) {
            case "Spring":
            case "Fall":
                switch (date.getDayOfWeek().getValue()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return 9;
                    case 6:
                        return 7;
                    case 7:
                        return 8;
                }
                break;
            case "Summer":
                switch (date.getDayOfWeek().getValue()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return 6;
                    case 6:
                        return 4;
                    case 7:
                        return 5;
                }
                break;
            case "Winter":
                switch (date.getDayOfWeek().getValue()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return 3;
                    case 6:
                        return 1;
                    case 7:
                        return 2;
                }
                break;
        }
        return -1;
    }

    private String getSeason(ZonedDateTime date) {
        String[] seasons = {
                "Winter", "Winter", "Spring", "Spring", "Summer", "Summer",
                "Summer", "Summer", "Fall", "Fall", "Winter", "Winter"
        };
        return seasons[date.getMonth().getValue() - 1];
    }


}

