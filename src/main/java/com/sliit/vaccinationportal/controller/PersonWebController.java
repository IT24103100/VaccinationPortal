package com.sliit.vaccinationportal.controller;

import com.sliit.vaccinationportal.model.Person;
import com.sliit.vaccinationportal.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/persons") // A base URL for the web interface to avoid clashes with the API
public class PersonWebController {

    @Autowired
    private PersonService personService;

    /**
     * Display a list of all persons.
     */
    @GetMapping
    public String viewHomePage(Model model) {
        model.addAttribute("listPersons", personService.getAllPersons());
        return "persons"; // Returns the persons.html template
    }

    /**
     * Show the form to add a new person.
     */
    @GetMapping("/new")
    public String showNewPersonForm(Model model) {
        Person person = new Person();
        model.addAttribute("person", person);
        return "add-person"; // Returns the add-person.html template
    }

    /**
     * Handle the form submission for saving a new person.
     */
    @PostMapping("/save")
    public String savePerson(@ModelAttribute("person") Person person) {
        personService.createPerson(person);
        return "redirect:/web/persons"; // Redirect back to the main list
    }

    /**
     * Show the form to edit an existing person.
     */
    @GetMapping("/edit/{id}")
    public String showEditPersonForm(@PathVariable("id") Long id, Model model) {
        Person person = personService.getPersonById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid person Id:" + id));
        model.addAttribute("person", person);
        return "edit-person"; // Returns the edit-person.html template
    }

    /**
     * Handle the form submission for updating a person.
     */
    @PostMapping("/update/{id}")
    public String updatePerson(@PathVariable("id") Long id, @ModelAttribute("person") Person person) {
        personService.updatePerson(id, person);
        return "redirect:/web/persons";
    }

    /**
     * Handle the deletion of a person.
     */
    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable("id") Long id) {
        personService.deletePerson(id);
        return "redirect:/web/persons";
    }
}