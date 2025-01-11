package com.finalproject.uni_earn.repo;

import com.finalproject.uni_earn.entity.Application;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepo extends CrudRepository<Application, Long> {
    static void Save(Application map) {
    }
}
