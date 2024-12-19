package org.yearup.data;

import org.yearup.models.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileDao
{
    Profile create(Profile profile);
    Profile getByUserId(int userId);
    List<Profile> getAllProfile();
    void updateProfile(Profile profile);
}