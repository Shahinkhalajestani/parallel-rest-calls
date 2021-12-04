package shahintraining.parallelrestcall.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shahintraining.parallelrestcall.services.ProcessDataService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parallelAndSerial")
public class ParallelAndSerialController {


    @Autowired
    private ProcessDataService processDataService;


    @PostMapping("/serial")
    public ResponseEntity serial(@RequestBody String[] listData){
        List<Map> maps = processDataService.runSerial(listData);
        return new ResponseEntity(maps, HttpStatus.OK);
    }

    @PostMapping("/parallel")
    public ResponseEntity<List<String>> parallel(@RequestBody String[] listData){
        List<String> stringList = processDataService.runParallel(listData);
        return new ResponseEntity<>(stringList, HttpStatus.OK);
    }

    @PostMapping("/parallel/completableFuture")
    public ResponseEntity parallelCompletableFuture(@RequestBody String[] listData){
        List<Map> maps = processDataService.runParallelWithCompletableFuture(listData);
        return new ResponseEntity(maps,HttpStatus.OK);
    }

    @PostMapping("/parallel/completableFutureV2")
    public ResponseEntity parallelCompletableFutureV2(@RequestBody String[] listData){
        List<Map> maps = processDataService.runParallelWithCompletableFutureV2(listData);
        return new ResponseEntity(maps,HttpStatus.OK);
    }


}
