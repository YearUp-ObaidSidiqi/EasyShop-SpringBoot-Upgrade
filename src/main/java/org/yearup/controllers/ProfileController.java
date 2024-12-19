package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("profile")
@CrossOrigin

public class ProfileController{

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao,UserDao userDao ) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @PreAuthorize("permitAll()")
    @PostMapping()
    public Profile create(@RequestBody Profile profile){
        return profileDao.create(profile);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("{userId}")
    public Profile getByUserId(@PathVariable int userId, Principal principal){

        String username = principal.getName();
        User user = userDao.getByUserName(username);

        // get profile by id
        Profile profile = profileDao.getByUserId(user.getId());

        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return profile;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("")
    public List<Profile> getAllProfile(){
        return profileDao.getAllProfile();
    }

    @PreAuthorize("permitAll()")
    @PutMapping()
    public void updateProfile (@RequestBody Profile profile, Principal principal){

        String username = principal.getName();
        int userId = userDao.getByUserName(username).getId();
        profile.setUserId(userId);

        profileDao.updateProfile(profile);
    }
}