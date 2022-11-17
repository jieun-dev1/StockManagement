package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import org.springframework.stereotype.Service;

@Service
public class OptimisticLockStockFacade {
  private OptimisticLockStockService optimisticLockStockService;

  public OptimisticLockStockFacade(
      OptimisticLockStockService optimisticLockStockService) {
    this.optimisticLockStockService = optimisticLockStockService;
  }

  public void decrease(Long id, Long quantity) throws InterruptedException {
    while (true) {
      //예외발생시에만 thread.sleep 후 다시 시도를 계속 하게 됨. 정상적으로 decrease 되면, break 탈출
      try{
        optimisticLockStockService.decrease(id, quantity);
        break;
      } catch(Exception e) {
        Thread.sleep(50);
      }
    }
    }

  }
