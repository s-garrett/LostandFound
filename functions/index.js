const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.messageNotification = functions.database.ref("messages/{pushId}")
.onCreate((snapshot, context) =>{
    const message = snapshot.child("messageText").val();

    const payload ={
      notification: {
        title: "New Message from: " + snapshot.child("messageUser").val(),
        body: message ? (message.length <= 100 ? message : message.substring(0, 97) + '...') :'',
        sound: "default"
      },
    };

    const options = {
      priority: "high",
      timeToLive: 10
    };

    return admin.messaging().sendToTopic("messageNotification", payload, options);

});
