# Android-vision

## Error Http-Post request

Steps involved:

* Upload image and get the downloaduri from firebase
* Make an Http post request to the url: https://cryptic-mesa-62652.herokuapp.com/url
* with body { "id": downloaduri} e.g : {"id":"http://opensourceforu.com/wp-content/uploads/2016/09/Figure-1-Sample-Page-1.jpg"}
* display the result
