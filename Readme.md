# GameKnight

GameKnight for android helps users in discovering android multiplayer games and also to interact with people while playing them creating an all round social experiance in mobile gaming.

### Features
  - Google+ Login
  - Group chat in different game rooms
  - App to app in game calling

### Working (Got to change this title)
- [Parse] is used to handle the database of users and the games available in the app. The profile pictures of users are obtained from  their Google+ account.
- The in-app messages are actually sent as JSON strings using the Sinch Messaging API which allows the app to understand what the message is meant for.
- The in-app calling uses the Sinch app to app calling API which is implemented inside a service to allow for the call to remain active during gameplay and also allows the users to easily end or mute the call from the notification area.

### Future imorovements
- Support for more multiplayer games can be added easily.
- The cross platform support of the Sinch SDK allows for an iOS client as well.
- New multiplayer game developers can contact us to make their game available in the app allowing for users to discover new content easily.
- Friends system which will allow users to add each other as friends and directly chat and play with each other.


[Parse]:http://www.parse.com/