package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

  private RedisLockRepository redisLockRepository;
  private StockService stockService;

  public LettuceLockStockFacade(RedisLockRepository redisLockRepository,
      StockService stockService) {
    this.redisLockRepository = redisLockRepository;
    this.stockService = stockService;
  }

  public void decrease(Long key, Long quantity) throws InterruptedException{
    //lock 획득 실패 시 100ms 이후에 시도.
    while(!redisLockRepository.lock(key)){
      Thread.sleep(100);
    }

    //lock 획득에 성공을 했따면,
    try {
      stockService.decrease(key, quantity);
    } finally {
      redisLockRepository.unlock(key);
    }
  }
}
