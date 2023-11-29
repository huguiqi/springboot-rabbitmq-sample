package com.example.demo.service.job;

import com.example.demo.service.counter.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;

@Slf4j
@Component
public class WritingComsumFileJob {

    @Value("${mq.counter.path}")
    private String baseFilePath;

    @Value("${mq.filepath.topic.name1}")
    private String topic1FileName;

    @Value("${mq.filepath.topic.name2}")
    private String topic2FileName;

    @Value("${mq.filepath.fanout.name1}")
    private String fanout1FileName;

    @Value("${mq.filepath.fanout.name2}")
    private String fanout2FileName;

    @Value("${mq.filepath.direct.name}")
    private String directFileName;

    @Value("${mq.filepath.sendmq.name}")
    private String sendMqFileName;



    @Scheduled(fixedDelay = 20000L)
    public void writingToFileForTopic1() {
        log.info("job-----start writingToFileForTopic1------");

        TotalCounterTopic1 counter1 = TotalCounterTopic1.getInstance();
        Long currentCountSize = counter1.givenCurrentValue();

        if (currentCountSize == null || currentCountSize.intValue() == 0) {
            log.warn("writingToFileForTopic1----无可存入的记数值!!");
            return;
        }

        BufferedOutputStream bos = null;
        try {
            String path = baseFilePath + topic1FileName;
            String result = readFile(path);
            Long originVal = 0L;
            if (StringUtils.hasText(result)){
                originVal = new Long(result);
                currentCountSize = currentCountSize + originVal;
            }
            bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            //写数据
            bos.write(currentCountSize.toString().getBytes());


        } catch (IOException e) {
            log.error("writingToFileForTopic1: 写入数据失败!!!", e);
        } finally {
            if (bos != null) {
                //释放数据
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("writingToFileForTopic1: 关闭流失败!!!", e);
                }
            }
        }

        log.info("job-----end writingToFileForTopic1------");
    }




    @Scheduled(fixedDelay = 20000L)
    public void writingToFileForTopic2() {
        log.info("job-----start writingToFileForTopic2------");

        TotalCounterTopic2 counter1 = TotalCounterTopic2.getInstance();
        Long currentCountSize = counter1.givenCurrentValue();

        if (currentCountSize == null || currentCountSize.intValue() == 0) {
            log.warn("writingToFileForTopic2----无可存入的记数值!!");
            return;
        }

        BufferedOutputStream bos = null;
        try {
            String path = baseFilePath + topic2FileName;
            String result = readFile(path);
            Long originVal = 0L;
            if (StringUtils.hasText(result)){
                originVal = new Long(result);
                currentCountSize = currentCountSize + originVal;
            }
            bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            //写数据
            bos.write(currentCountSize.toString().getBytes());


        } catch (IOException e) {
            log.error("writingToFileForTopic2: 写入数据失败!!!", e);
        } finally {
            if (bos != null) {
                //释放数据
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("writingToFileForTopic2: 关闭流失败!!!", e);
                }
            }
        }

        log.info("job-----end writingToFileForTopic2------");
    }



    @Scheduled(fixedDelay = 20000L)
    public void writingToFileForFanout1() {
        log.info("job-----start writingToFileForFanout1------");

        TotalCounterFanout1 counter1 = TotalCounterFanout1.getInstance();
        Long currentCountSize = counter1.givenCurrentValue();

        if (currentCountSize == null || currentCountSize.intValue() == 0) {
            log.warn("writingToFileForFanout1----无可存入的记数值!!");
            return;
        }

        BufferedOutputStream bos = null;
        try {
            String path = baseFilePath + fanout1FileName;
            String result = readFile(path);
            Long originVal = 0L;
            if (StringUtils.hasText(result)){
                originVal = new Long(result);
                currentCountSize = currentCountSize + originVal;
            }
            bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            //写数据
            bos.write(currentCountSize.toString().getBytes());


        } catch (IOException e) {
            log.error("writingToFileForFanout1: 写入数据失败!!!", e);
        } finally {
            if (bos != null) {
                //释放数据
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("writingToFileForFanout1: 关闭流失败!!!", e);
                }
            }
        }

        log.info("job-----end writingToFileForFanout1------");
    }


    @Scheduled(fixedDelay = 20000L)
    public void writingToFileForFanout2() {
        log.info("job-----start writingToFileForFanout2------");

        TotalCounterFanout2 counter1 = TotalCounterFanout2.getInstance();
        Long currentCountSize = counter1.givenCurrentValue();

        if (currentCountSize == null || currentCountSize.intValue() == 0) {
            log.warn("writingToFileForFanout2----无可存入的记数值!!");
            return;
        }

        BufferedOutputStream bos = null;
        try {
            String path = baseFilePath + fanout2FileName;
            String result = readFile(path);
            Long originVal = 0L;
            if (StringUtils.hasText(result)){
                originVal = new Long(result);
                currentCountSize = currentCountSize + originVal;
            }
            bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            //写数据
            bos.write(currentCountSize.toString().getBytes());


        } catch (IOException e) {
            log.error("writingToFileForFanout2: 写入数据失败!!!", e);
        } finally {
            if (bos != null) {
                //释放数据
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("writingToFileForFanout2: 关闭流失败!!!", e);
                }
            }
        }

        log.info("job-----end writingToFileForFanout2------");
    }

    @Scheduled(fixedDelay = 20000L)
    public void writingToFileForDirect() {
        log.info("job-----start writingToFileForDirect------");

        TotalCounterDirect counter1 = TotalCounterDirect.getInstance();
        Long currentCountSize = counter1.givenCurrentValue();

        if (currentCountSize == null || currentCountSize.intValue() == 0) {
            log.warn("writingToFileForFanout----无可存入的记数值!!");
            return;
        }

        BufferedOutputStream bos = null;
        try {
            String path = baseFilePath + directFileName;
            String result = readFile(path);
            Long originVal = 0L;
            if (StringUtils.hasText(result)){
                originVal = new Long(result);
                currentCountSize = currentCountSize + originVal;
            }
            bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            //写数据
            bos.write(currentCountSize.toString().getBytes());


        } catch (IOException e) {
            log.error("writingToFileForFanout: 写入数据失败!!!", e);
        } finally {
            if (bos != null) {
                //释放数据
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("writingToFileForFanout: 关闭流失败!!!", e);
                }
            }
        }

        log.info("job-----end writingToFileForFanout------");
    }


    @Scheduled(fixedDelay = 20000L)
    public void writingToFileForSendMq() {
        log.info("job-----start writingToFileForSendMq------");

        TotalCounterSendMQ counter1 = TotalCounterSendMQ.getInstance();
        Long currentCountSize = counter1.givenCurrentValue();

        if (currentCountSize == null || currentCountSize.intValue() == 0) {
            log.warn("writingToFileForSendMq----无可存入的记数值!!");
            return;
        }

        BufferedOutputStream bos = null;
        try {
            String path = baseFilePath + sendMqFileName;
            String result = readFile(path);
            Long originVal = 0L;
            if (StringUtils.hasText(result)){
                originVal = new Long(result);
                currentCountSize = currentCountSize + originVal;
            }
            bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            //写数据
            bos.write(currentCountSize.toString().getBytes());


        } catch (IOException e) {
            log.error("writingToFileForSendMq: 写入数据失败!!!", e);
        } finally {
            if (bos != null) {
                //释放数据
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("writingToFileForSendMq: 关闭流失败!!!", e);
                }
            }
        }

        log.info("job-----end writingToFileForSendMq------");
    }



    public java.lang.String readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()){
            return "";
        }
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        try {
            fileInputStream = new FileInputStream(filePath);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            int bytesRead = 0;
            while (-1 != (bytesRead = bufferedInputStream.read(buff, 0, buff.length))) {
                bao.write(buff, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.warn("文件不存在，需要创建文件");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                bufferedInputStream.close();
                buff = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bao.toString();
    }

}
