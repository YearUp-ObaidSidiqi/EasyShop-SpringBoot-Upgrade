package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController{
    private ProfileDao profileDao;

    @Autowired
    public ProfileController(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    @PostMapping()
    @PreAuthorize("permitAll()")
    public Optional<Profile> create(@RequestBody Profile profile){
        try{
            return Optional.ofNullable(profileDao.create(profile));
        }
        catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Optional<Profile> getProfileById(@PathVariable int id){
        if(profileDao.getByUserId(id) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not Found");
        }
        return Optional.ofNullable(profileDao.getByUserId(id));
    }

    // add the appropriate annotation for a get action
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Profile> getAll(){
        // find and return all profile
        return profileDao.getAllProfile();
    }

    @PutMapping()
    @PreAuthorize("permitAll()")
    @ResponseStatus(value = HttpStatus.OK)
    public Optional<Profile> updateProfile(@RequestBody Profile profile){
        return Optional.ofNullable(profileDao.updateProfile(profile));
    }
}