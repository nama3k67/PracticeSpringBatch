package fpt.project.chunkTasklet.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import fpt.project.chunkTasklet.model.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtils {
    private final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private String fileName;
    private CSVReader csvReader;
    private CSVWriter csvWriter;
    private FileReader fileReader;
    private FileWriter fileWriter;
    private File file;

    public FileUtils(String fileName) {
        this.fileName = fileName;
    }

    public Line readLine() {
        try {
            if (csvReader == null) {
                initReader();
            }
            String[] line = csvReader.readNext();

            if (line == null) {
                return null;
            }
            return new Line(line[0], LocalDate.parse(line[1], DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        } catch (Exception e) {
            logger.error("Error while reading line in file: " + this.fileName);
            return null;
        }
    }

    public void writeLine(Line line) {
        try {
            if (csvWriter == null) {
                initWriter();
            }
            String[] lineStr = new String[2];
            lineStr[0] = line.getName();
            lineStr[1] = line.getAge().toString();

            csvWriter.writeNext(lineStr);
        } catch (Exception e) {
            logger.error("Error while writing line in file: " + this.fileName);
        }
    }

    private void initReader() throws Exception {
        if (file == null) {
            file = new File(fileName);
        }

        if (fileReader == null) {
            fileReader = new FileReader(file);
        }

        if (csvReader == null) {
            csvReader = new CSVReader(fileReader);
        }
    }

    private void initWriter() throws Exception {
        if (file == null) {
            file = new File(fileName);
            file.createNewFile();
        }

        if (fileWriter == null) {
            fileWriter = new FileWriter(file, true);
        }

        if (csvWriter == null) {
            csvWriter = new CSVWriter(fileWriter);
        }
    }

    public void closeWriter() {
        try {
            csvWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            logger.error("Error while closing writer.");
        }
    }

    public void closeReader() {
        try {
            csvReader.close();
            fileReader.close();
        } catch (IOException e) {
            logger.error("Error while closing reader.");
        }
    }
}
