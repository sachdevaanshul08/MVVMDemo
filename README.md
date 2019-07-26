# About The Project - `GoodsDelivery`

This Android App makes use of some free api to fetch the delivery(Random) data and displays it on the screen by leveraging the power of `MVVM Architecture pattern` backed up by Android Architecture Components.
It consists of two pages - one is Home Screen which shows the data in [RecyclerView] and other one is Details Page which is triggered on clicking on any item on the Home screen.

This whole project is written in `Kotlin` and it also includes the static code analyser tool (Lint) to make sure that the code adhere to the industry standards.
Unit test(s) are also covered in this project.


### Components used in the Project -

This Android app demonstrate the `MVVM Architecture pattern` by using the Android Architecture components like [Room] for storing the data locally, Repository using [Paging] that will use the local database to page in data for the UI and also back-fill the database from the network as the user reaches to the end of the data in the database.
[Retrofit] networking library is being used to fetch the data from the server. At the front end, [DataBinding] library is taking care of the data population on XML layouts using objects which is being observed using [LiveData] encapsulated in [ViewModel] and [Espresso] and [Mockito] has taken the responsibility to handle the testing.
Last but most importantly, [Dagger2] is generating all the boilerplate code for us, taking care of dependency injection in our project and is based on Annotation.

### About the API

API is sending the list of json objects in following format.
To Consume the api, send the starting index (Offset) and number of items required (Limit) as query string parameters.

```
{
   "id":1,
   "description":"description",
   "imageUrl":"image_url",
   "location":{
      "lat":12.2345678,
      "lng":-78.45678,
      "address":"address"
   }
}
```


### How To Run This Program :-

1. Download/clone this project on your system.
2. [Download Android Studio] and install it on your system.
3. Import the app/project you have downloaded in earlier step.
4. Run it either on Emulator or Mobile device.(Refer to [How to run Android App])
5. Congrats! You have successfully executed this app on/from your machine.

This app is also using Google maps to show the marker on the map on the Details screen therefore you will have to put the Google_Maps_Key from your account onto this project.

[How to obtain Maps Api Key]

Location of the config file is described in next steps.

### Additional Information
There are two scripts in the root/config folder :-
1. `appconfig.gradle` - This gradle file contains the app configuration like baseUrl and google maps key etc.
2. `lintanalyzer.gradle` - This contains the lint configuration.You can add more like static analyser tools like Checkstyle, PMD and findBugs etc.

Reference(s) - [https://developer.android.com/jetpack/docs/guide]


[Download Android Studio]:<https://developer.android.com/studio>
[How to run Android App]:<https://developer.android.com/training/basics/firstapp/running-app>
[RecyclerView]:<https://developer.android.com/guide/topics/ui/layout/recyclerview>
[Room]:<https://developer.android.com/topic/libraries/architecture/room>
[Paging]:<https://developer.android.com/topic/libraries/architecture/paging>
[Retrofit]:<https://square.github.io/retrofit/>
[DataBinding]:<https://developer.android.com/topic/libraries/data-binding>
[LiveData]:<https://developer.android.com/topic/libraries/architecture/livedata>
[ViewModel]:<https://developer.android.com/topic/libraries/architecture/viewmodel>
[Espresso]:<https://developer.android.com/training/testing/espresso>
[Mockito]:<https://developer.android.com/training/testing/unit-testing/local-unit-tests>
[Dagger2]:<https://dagger.dev/>
[https://developer.android.com/jetpack/docs/guide]:<https://developer.android.com/jetpack/docs/guide>
[How to obtain maps Api Key]:<https://developers.google.com/maps/documentation/android-sdk/get-api-key>