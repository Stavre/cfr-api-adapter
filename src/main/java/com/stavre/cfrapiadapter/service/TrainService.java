package com.stavre.cfrapiadapter.service;

import com.stavre.cfrapiadapter.adapter.TrainAdapter;
import com.stavre.cfrapiadapter.dto.enriched.EnrichedTrainDto;
import com.stavre.cfrapiadapter.dto.scraper.TrainDto;
import com.stavre.cfrapiadapter.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final TrainAdapter trainAdapter;

    public EnrichedTrainDto getTrainStops(String trainId, String date) {
        TrainDto scraped = trainRepository.getTrainStops(trainId, date);
        return trainAdapter.adapt(scraped, date);
    }
}
