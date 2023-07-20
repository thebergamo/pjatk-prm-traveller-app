# pjatk-prm-traveller-app

This project contains an Android project developed in accordance to the requirements from PRM subject at PJATK

## Firebase

This project uses Firebase. In order to make it working, please initialize Firebase in this project and include the [google-services.json](app/google-services.json) file.

## Task

The content of the project enriched with demonstrative assessment criteria.

In the assessment of the project, apart from practical and substantive correctness, the quality and legibility of the code written by you will also be taken. [2p]

The project should be implemented using native Android SDK tools in kotlin language.

### Traveler

The task is to create an application to manage and save and read the list of visited places using an external database (e.g. firebase or own backend implementation).

The application should have:

- Login/authentication screen [2p]:
  login by email address and password or other method using an identity provider (e.g. Facebook login). In addition, if the user does not have an account, he should be able to create one.
- Visited places list screen [2p]:
  statement representing the list of visited places of the user (each logged in user should have their own list of places). Each item in the list should have the following information: place name, diameter of the circle encompassing the place, and photograph of the place.
- Detail screen [2p]:
  containing a photo of the place or the default graphic replacing it, the name of the place and a short note about it added by the user when creating or editing the place.
- Add/edit screen [5p]:
  responsible for adding new places to the list. With the option of naming the place, taking an additional note (<500 characters), taking a photo of the place. In addition, with saving the current location of the device or choosing a location from the map. The screen should also allow modification and removal of the place.
- Map screen [3p]:
  with marked places and their circles area (you can use Google Maps SDK or any other, e.g. HERE).

The application should notify (notification in the statusbar [2p]) of entering into any of the saved place area (Geofence mechanism or other similar [2p]).
