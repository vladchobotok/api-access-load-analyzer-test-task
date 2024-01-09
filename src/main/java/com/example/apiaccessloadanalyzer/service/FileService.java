package com.example.apiaccessloadanalyzer.service;

import com.example.apiaccessloadanalyzer.model.RequestModel;
import com.example.apiaccessloadanalyzer.utils.CounterManager;
import com.example.apiaccessloadanalyzer.utils.RequestValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class FileService {

    private final RequestValidator requestValidator;
    private final CounterManager counterManager;

    @Autowired
    public FileService(RequestValidator requestValidator, CounterManager counterManager) {
        this.requestValidator = requestValidator;
        this.counterManager = counterManager;
    }

    public List<RequestModel> convertFileToRequestModelList(MultipartFile file) throws IOException {
        List<RequestModel> requestModelList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try{
                    RequestModel requestModel = parseLine(line);
                    if (requestValidator.validateRequestModel(requestModel)) {
                        requestModelList.add(requestModel);
                        counterManager.incrementValidRows();
                    }
                }
                catch(Exception e){
                    log.error("Error while parsing file, line " + counterManager.getNextIndexOfTotalRows() + ": " + e);
                }
                counterManager.incrementTotalRows();
            }
        }
        return requestModelList;
    }

    private RequestModel parseLine(String line) {
        String[] parts = line.split(";");

        RequestModel requestModel = new RequestModel();
        requestModel.setIp(parts[0]);
        requestModel.setDateTime(LocalDateTime.parse(parts[1], DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ssZ")));
        requestModel.setRequestMethod(parts[2]);
        requestModel.setUri(parts[3]);
        requestModel.setRequestStatus(Integer.parseInt(parts[4]));

        return requestModel;
    }
}
