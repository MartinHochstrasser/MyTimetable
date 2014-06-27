MyTimetable
-----------

This project is a proof-of-concept Android App to showcase the cababilities of the Android Wear Preview SDK.

It was created for the [IO Extended Zürich Android Wear Hackathon](http://www.hackathon.io/io14)

_MyTimetable_ takes the user's location to automatically query online timetable information in the background and show it to the user on the Android Wear device.

Checkout the [project's presentation](http://goo.gl/bG4EbE)

## How it works

* Takes the devices current location to periodically query nearby stations in the background
* Creates geofences with the station data
* When entering a geofence, queries current timetable data for the station
* Shows the time table information as notification (on phone and Wear device)
* The user can initiate a connection query directly from the Wear device

The station and timetable data is queried from [http://transport.opendata.ch](http://transport.opendata.ch). (Thanks a lot guys for the cool API)

## How to build and run

1. Checkout the source and import into Android Studio
2. Download the Android Wear Developer Preview support Jar and copy it into *app/wearable_preview_lib* (You need to [sign up](http://developer.android.com/wear/preview/start.html) to download the developer preview)
3. Connect your Android device to the Wear Preview Emulator
4. Build and run as normal app on your Android device


## Functional limitations

Due to the fact that this is a proof-of-concept app, there are (still) a few functional limitations:

* Can only query time table information for Switzerland
* Does not work well for big train stations and indoors
* Very limited error handling
* As the focus of this project is the Wear device, there is only a settings UI for the phone
* Connection query does not work if the entered station name is unknown to the API
* Connection query can only be initiated from the Wear device

## Backlog (stuff to add)

Things I would like to add when I get the time:

* Move from Wear Preview SDK to real Wear SDK
* Document code better ;-)
* Add check for station name for query and present user with choice
* Custom Views for Wear device (make it look nicer)
* Also show information on the phone
* Add better error handling (e.g. Google play services)


