package com.starwars.batch.processor;

import com.starwars.batch.domain.People;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class PeopleProcessor implements ItemProcessor<People,People>{

    private static final String UNDEFINED_GENDER = "n/a";
    private static final String DROID_GENDER = "Droid";

    @Override
    public People process(People people) throws Exception {
        People processedPeople = (People) people.clone();
        if(people.getGender().equals(UNDEFINED_GENDER)){
            processedPeople.setGender(DROID_GENDER);
        }
        return processedPeople;
    }
}
