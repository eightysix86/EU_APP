'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/students/{receiver_id}/Notification/{notification_id}')
		.onWrite((snapshot, context) => {

  			const receiver_id = context.params.receiver_id;

  			const notification_id = context.params.notification_id;
	 
	   	//	const receiver_id = event.params.receiver_id;

	   	//	const notification_id = event.params.notification_id;

	   		console.log('receiver_id', receiver_id);
	   		console.log('notification_id :', notification_id);

	   		const afterData = snapshot.after.val();

			if (!afterData){
			    return console.log('A notification has been deleted from the database : ', notification_id);
			//    return null;
					}

	   	/*	if(!context.params.data.val())
	   		{
	   			return console.log('A notification has been deleted from the database : ', notification_id);
	   		}*/


	   		 const deviceToken = admin.database().ref(`/students/${receiver_id}/deviceToken`).once('value');

	   		console.log('deviceToken =', deviceToken);

	   		return deviceToken.then(result => 
	   		{
	   			const token_id = result.val();
	   			console.log('token_id =', token_id);
	   			const payload = 
	   			{
	   				notification:
	   				{
	   					title: "Запит в друзі",
	   					body: "У вас новий запит в друзі",
	   					icon: "default"
	   		 	     }
	 	  		};

	 	  		return admin.messaging().sendToDevice(token_id, payload)
	 	  					.then(response =>
	 	  					{
	 	  					 return	console.log('This wa the notification feature.');
	 	  					 //   return null;
	 	  					});
	 	  				})

	   			});



