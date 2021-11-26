# Scavenger Hunt
#### By Dan Balboni and Cole Stainsby

## Description:
  Users will participate in a scavenger hunt set up by another user, a scavenger hunt admin.  This scavenger hunt admin will be able to pin locations, add required tasks, and prompt all users.  There will be options to add a time limit, a max speed(no cars), and a map boundary.  The scavenger hunt tasks will be verified either by location or by submitting an image of the required task which will be verified by the scavenger hunt admin upon scavenger hunt completion.  A game chat will allow all users and admin to chat during the game.  A user will make their own account which will save past stats and achievements.  Stakeholders will be GU students/app users.  We will source a Google Maps API from Google. Impact is a dope game :).

## Implementation:
1. OOP (Classes)
  User account
  Two player types: GameAdmin and GameParticipant extend GamePlayer
  ScavengerHuntGame object
  ScavengerTask object
  sqliteDatabase object
2. data structures
  User info database
  Login info
  Achievements/stats
3. library dependencies 
  Google Maps API
4. new topics
  Firebase 
  geofencing - admin can set boundaries


## Core functionality:
1. The users will be able to use Google maps to find the pinned locations made by the admin.
2. Query local google maps locations for scavenger task ideas
3. Firebase for messaging system
4. Image intents 
6. SQLite database for user data
7. Geofencing 
8. Matchmaking queue using firebase

## Firebase
[Setup Firebase On Android](https://www.youtube.com/watch?v=dRYnm_k3w1w&t=624s)

