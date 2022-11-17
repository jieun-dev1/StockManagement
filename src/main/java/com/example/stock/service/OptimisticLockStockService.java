package com.example.stock.service;


import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticLockStockService {
  private StockRepository stockRepository;

  public OptimisticLockStockService(StockRepository stockRepository) {
    this.stockRepository = stockRepository;
  }

  @Transactional
  public void decrease(Long id, Long quantity){
    //Lock을 걸고 데이터를 가지고 온다
    Stock stock = stockRepository.findByIdWithOptimisticLock(id);
    stock.decrease(id);
    stockRepository.saveAndFlush(stock);
  }}
