package com.stavre.cfrapiadapter.adapter;

import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainMetadataDto;
import com.stavre.cfrapiadapter.dto.train.TrainMetadataDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainMetadataAdapter {

    public EnrichedTrainMetadataDto adapt(Optional<TrainMetadataDto> trainDtoOptional) {
        if (trainDtoOptional.isEmpty()) {
            return new EnrichedTrainMetadataDto(List.of("Could not scrape train from CFR page"));
        }

        TrainMetadataDto trainMetadataDto = trainDtoOptional.get();
        List<String> errors = new ArrayList<>();

        String trainNumber = getNumber(trainMetadataDto.trainNumber(), errors);
        String trainCategory = getCategory(trainMetadataDto.trainCategory(), errors);

        String trainId = getTrainId(trainCategory, trainNumber);
        String trainOperator = getTrainOperator(trainMetadataDto.operator(), errors);

        return new EnrichedTrainMetadataDto(trainId, trainCategory, trainNumber, trainOperator, errors);

    }

    private String getTrainOperator(String operator, List<String> errors) {
        if (operator.isBlank()) {
            errors.add("Train operator is blank.");
            return null;
        }

        return operator.trim();
    }

    private String getTrainId(String trainCategory, String trainNumber) {
        return "%s %s".formatted(trainCategory, trainNumber);
    }

    private String getCategory(String category, List<String> errors) {
        if (category.isBlank()) {
            errors.add("Train category is blank.");
            return null;
        }

        return category.trim();
    }

    private String getNumber(String number, List<String> errors) {
        if (number.isBlank()) {
            errors.add("Train number is blank.");
            return null;
        }

        return number.trim();
    }
}
