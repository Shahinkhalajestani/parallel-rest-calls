package shahintraining.parallelrestcall.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ProcessDataService {

    private RestTemplate restTemplate;

    @Autowired
    public ProcessDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProcessDataService() {
    }

    public List<Map> runSerial(String[] listData){
//        List<String> result = new ArrayList<>();
//        for (String data : listData) {
//            processData(data , result);
//        }
//        return result;
        return Arrays.stream(listData).map(s -> processData(s)).collect(Collectors.toList());
    }



    public List<String> runParallel(String[] listData){
        List<String> result = new ArrayList<>();
        Arrays.asList(listData).stream().parallel().forEach(s -> processData(s,result));
        return result;
    }

    public List<Map> runParallelWithCompletableFuture(String[] listData){
       return Arrays.stream(listData).map(s -> CompletableFuture.supplyAsync(() ->
                processData(s)).exceptionally(throwable -> {
                    Map<String,String> errResult = new LinkedHashMap<>();
                    errResult.put("errorCode ","500");
                    errResult.put("errorMsg ",throwable.getMessage());
            return errResult;
        })).map(CompletableFuture::join).collect(Collectors.toList());
    }



    public List<Map> runParallelWithCompletableFutureV2(String[] listData){
        List<CompletableFuture<Map>> futures = Arrays.stream(listData).map(s -> CompletableFuture.supplyAsync(() ->
                processData(s)).exceptionally(throwable -> {
            Map<String, String> errResult = new LinkedHashMap<>();
            errResult.put("errorCode ", "500");
            errResult.put("errorMsg ", throwable.getMessage());
            return errResult;
        })).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        List<Map> result = new ArrayList<>();
        futures.forEach(future -> {
            try {
                result.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    private Map processData(String data){
        Map firePlans = restTemplate
                .getForObject("http://10.30.90.25:6200/v1/datin/fire/plans",Map.class);
        return firePlans;
    }

    private void processData(String data, List<String> result) {
        Object forObject = restTemplate.getForObject("http://10.30.90.25:6200/v1/datin/fire/plans", Object.class);
        result.add(data);
    }


}
