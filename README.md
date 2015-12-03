# SafetyApp

Safety Feature of Online Cab Booking Android Application

Root file : /app/src/main/AndroidManifest.xml -> It provides manifest definitions, such as permissions and all the activity files.

MainActivity.java is the the class used to display the UI screen which is displayed when the application is opened.This is class is mapped to the XML "/app/src/main/res/layout/activity_main.xml" where the UI components are defined. Manifest file defines which activity to be launched(in our case MainActivity.java), “onCreate” method is called and defines which template to call and is used to define any onClickListeners.

![alt tag](https://cloud.githubusercontent.com/assets/10440045/11553506/242ff178-9944-11e5-8b3f-a11cb5965e04.png)


SOS() method inside “onClickListener” is the HELP button to send an SOS message



button1 is the keep me safe button to send details of your cab




“SettingsActivity.java” is the class that gives the settings options by calling the method “bindPreferenceSummaryToValue” and when value is changed it is saved to the file system of the phone in “SharedPreferences”.




FetchData.java - reads number from settings.

“MapsActivity.java”  on instantiation calls “onCreate” method which is mapped to “app/src/main/res/layout/activity_maps.xml” for the feature “Where Am I”. SupportMapFragment is the API by google which is used to call maps, and is used in the method “openShareImageDialog” which provide our share button. Inside this methos is the object “intent” which is used to send something, in this case an image from maps.



Pending Feature : Track a cab


