package org.example;

import com.regula.documentreader.webclient.api.DocumentReaderApi;
import com.regula.documentreader.webclient.model.*;
import com.regula.documentreader.webclient.model.ext.ProcessRequestImage;
import com.regula.documentreader.webclient.model.ext.RecognitionParams;
import com.regula.documentreader.webclient.model.ext.RecognitionRequest;
import com.regula.documentreader.webclient.model.ext.RecognitionResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");

        String imageBase64 = FileReaderUtil.readTextFile("image_base64.txt");

        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
        var image = new ProcessRequestImage(imageBytes, Light.WHITE);


        var requestParams = new RecognitionParams()
                .withScenario(Scenario.FULL_PROCESS)
                .withResultTypeOutput(Result.STATUS, Result.TEXT, Result.IMAGES);


        RecognitionRequest request = new RecognitionRequest(requestParams, List.of(image));

        var api = new DocumentReaderApi("https://api.regulaforensics.com", false);
        RecognitionResponse response = api.process(request);


        if (response != null) {
            System.out.println("A response came in.");

            if (response.getOriginalResponse() != null
                    && response.getOriginalResponse().getContainerList() != null
                    && response.getOriginalResponse().getContainerList().getList() != null) {

                List<ResultItem> resultItems = response.getOriginalResponse().getContainerList().getList();

                List<ExtractedData> extractedDataList2 = resultItems
                        .stream()
                        .filter(resultItem -> resultItem instanceof TextResult)
                        .flatMap(resultItem -> {
                            TextResult textResult = (TextResult) resultItem;
                            return textResult.getText().getFieldList().stream();
                        })
                        .map(textField -> {
                            ExtractedData extractedData = new ExtractedData();
                            extractedData.setFieldName(textField.getFieldName());
                            extractedData.setFieldType("TEXT");
                            extractedData.setValue(textField.getValue());
                            return extractedData;
                        })
                        .collect(Collectors.toList());


                List<ExtractedData> extractedDataList3 = resultItems
                        .stream()
                        .filter(resultItem -> resultItem instanceof ImagesResult)
                        .flatMap(resultItem -> {
                            ImagesResult imagesResult = (ImagesResult) resultItem;
                            return imagesResult.getImages().getFieldList().stream();
                        })
                        .filter(imagesField -> !imagesField.getValueList().isEmpty())
                        .map(imagesField -> {
                            ExtractedData extractedData = new ExtractedData();
                            extractedData.setFieldName(imagesField.getFieldName());
                            extractedData.setFieldType("IMAGE");
                            extractedData.setValue(imagesField.getValueList().get(0).getValue());
                            return extractedData;
                        })
                        .collect(Collectors.toList());

                extractedDataList2.addAll(extractedDataList3);

                System.out.println(extractedDataList2);

            }
        } else {
            System.out.println("No response");
        }


    }
}