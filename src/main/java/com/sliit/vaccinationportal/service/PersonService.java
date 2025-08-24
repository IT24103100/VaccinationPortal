package com.sliit.vaccinationportal.service;

import com.sliit.vaccinationportal.model.Person;
import com.sliit.vaccinationportal.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    // Create
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    // Read (Get all)
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    // Read (Get one by ID)
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    // Update
    public Person updatePerson(Long id, Person personDetails) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));

        person.setName(personDetails.getName());
        person.setEmail(personDetails.getEmail());
        return personRepository.save(person);
    }

    // Delete
    public void deletePerson(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found with id: " + id));
        personRepository.delete(person);
    }
}