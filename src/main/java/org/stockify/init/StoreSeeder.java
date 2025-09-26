package org.stockify.init;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.stockify.dto.request.store.StoreRequest;
import org.stockify.model.service.StoreService;


@AllArgsConstructor
@Getter
@Component
public class StoreSeeder {

    private final StoreService storeService;

    public void ensureDefault(){
        if(!storeService.existsById(1L)){
            StoreRequest storeDefault = new StoreRequest("default","placeholder","not specified");
            storeService.save(storeDefault);
        }
    }
}
