package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class StockServiceTest {

  @Autowired
  private PessimisticLockStockService stockService;
//  @Autowired
//  private StockService stockService;
  @Autowired
  private StockRepository stockRepository;

  @BeforeEach
  public void before(){
    Stock stock = new Stock(1L, 100l);
    stockRepository.saveAndFlush(stock);
  }

  @AfterEach
  public void after(){
    stockRepository.deleteAll();
  }

  @Test
  public void stock_decrease(){
    stockService.decrease(1L, 1L);
    Stock stock = stockRepository.findById(1L).orElseThrow();
    assertEquals(99, stock.getQuantity());
  }

  @Test
  public void 동시에_100개_요청() throws InterruptedException {
    int threadCount = 100;
    //비동기로 실행하는 작업 단순화하여 실행할 수 있게 도와주는 API
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    //100개의 요청이 끝날때까지 기다려야 함. 다른 스레드에서 진행중인 작업이 완료 될 때까지 대기할 수 있도록 도와줌.
    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i=0;i<threadCount;i++){
      executorService.submit(()->{
        try{
          stockService.decrease(1L, 1l);
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await();

    Stock stock = stockRepository.findById(1L).orElseThrow();
    //100개 저장함. - 1개씩 100번 감소 시킴.
    assertEquals(0L, stock.getQuantity());
  }
}
