var uniqueId = function() {
	var date = Date.now();
	var random = Math.random() * Math.random();
	return Math.floor(date * random).toString();
};

var theMessage = function(text, user, id_) {
	if (id_=='') {
		return {
			user: user,
			text: text,
			id: uniqueId()
		};
	}
	else
		return{
			user: user,
			text: text,
			id: id_
		};

};



var appState = {
	name : '',
	mainUrl : 'http://localhost:8080/chat',
	history:[],
	token : 'TE11EN'
};


function run(){
	var status = document.getElementsByClassName('status')[0];
	status.innerText = 'Server is available';
	var sendButton = document.getElementsByClassName("butSend")[0];
	var newMessageBox = document.getElementById('newMessage');
	sendButton.addEventListener('click', onAddMessageClick);
	var newName = document.getElementById('changeName');
    newName.addEventListener('click', onChangeNameClick);
    var selectMessage = document.getElementsByClassName('box')[0];
	selectMessage.addEventListener('click', onSelectMessageClick);
	var delMessage = document.getElementsByClassName('messageBox')[0];
	delMessage.addEventListener('click', onDeleteMessageClick);
	var editMessage = document.getElementsByClassName('messageBox')[0];
	var saveMessage = document.getElementsByClassName('messageBox')[0];
	editMessage.addEventListener('click',onEditMessageClick);
	saveMessage.addEventListener('click', onSaveMessageClick);
	doPolling();
	
}

function onAddMessageClick() {
	var id_ = '';
    var messageText = document.getElementById('newMessage').value;
    if (messageText=='')
    	return;
    var sender = document.getElementById('Name').innerText;
    appState.name = sender;
	var newMessage = theMessage(messageText, appState.name, id_);

	if(messageText.value == '')
		return;

	document.getElementById('newMessage').value = '';
	console.log(messageText);
	sendMessage(newMessage, function() {
		console.log('Message sent ' + newMessage.text);

	});
}

function onSaveMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butSave')){
		var message = document.getElementById('newMessage');
		saveMessage(message.value);
		message.value = '';
    }

}

function saveMessage(newText){
	var divList = document.body.children[1];
    var list = divList.children[0];
    var i=0;
    var mes = 0;
    var text = document.getElementById('newMessage');
    var editId;
    var newMes;
	for (i=list.children.length-1; i>=0;i--){
		if ((checkSelectMessage(list.children[i]))!=0){
	        mes = list.children[i];
	        editId = list.children[i].id;
	        
	        newMes = theMessage(newText, appState.history[i].userName, editId);
	        
	        mes.classList.remove('select');
	        mes.children[1].innerText = newText;
	        putMessage(newMes, function() {
			console.log('Message put. New text: ' + newMes.text);

		});
	    	break;
    	}
	}
	 
}

function onEditMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butEdit')){
		var divList = document.body.children[1];
        var list = divList.children[0];
        var i=0;
        var mes = 0;
        var text = document.getElementById('newMessage');
    	for (i=list.children.length-1; i>=0;i--){
			if ((checkSelectMessage(list.children[i]))!=0){
	            mes = list.children[i];
	            text.value = mes.children[1].innerText;
	        	return;
       		}
		}
    }
}

function onDeleteMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butDelete')){
		var delMes;
		var i=0;
		var j=0;
		var divList = document.body.children[1];
        var list = divList.children[0];
        var hist = appState.history;
        var deleteId;
        while (j < list.children.length){
        	i=0;
	        while ((i < list.children.length) && (checkSelectMessage(list.children[i]))==0) 
	        	i++;
	        if (i==list.children.length)
	        	break;
			if ((checkSelectMessage(list.children[i]))!=0){
				j=i;
				deleteId = hist[j].id;
				
				delMes = theMessage(hist[j].textMessage, hist[j].userName, deleteId);
				var index = j;
				deleteMessage(delMes, function(){
					console.log('Message delete, id =  ' + deleteId );
					hist.splice(index,1);
					appState.token= 'TN' + (hist.length*8+11).toString() +'EN';
				});
				list.removeChild(list.children[j]);
				break;
			}
    	}
	}
}


function deleteMessage(mes, continueWith) {
	del(appState.mainUrl, JSON.stringify(mes), function(){
		continueWith && continueWith();
	});
}

function onSelectMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.nodeName=='SPAN'){
		var mes = evtObj.target.parentElement;
		if(mes.classList.contains('select')) {
			mes.classList.remove('select');
		}
		else {
			mes.classList.add('select');
		}
	}
}

function onChangeNameClick(evtObj) {
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butSign')){
		var val = document.getElementById('newN');
	changeName(val.value);
	val.value = '';
	}
}

function checkSelectMessage(message){
	
	if (message.classList.contains('select'))
			return message;
	else return 0;
}

function changeName(value) {
	if((!value)||(value=='User Name')){
		return;
	}
	var item = document.getElementById('Name');
	item.innerText = value;
	appState.name = value;
}

function sendMessage(message, continueWith) {
	post(appState.mainUrl, JSON.stringify(message), function(){
		continueWith && continueWith();
	});
}
function putMessage(message, continueWith) {
	put(appState.mainUrl, JSON.stringify(message), function(){
		continueWith();
	});
}

function createMessage(newMes){
	var date = new Date();
	var time = document.createTextNode (date.getHours() + ":" + date.getMinutes() + " ");
	var divItem = document.createElement('div');
	divItem.setAttribute('id', newMes.id);
	var pr = document.createElement('span');
	var mess = document.createElement('span');
	var node = document.createTextNode('');
	pr.appendChild (node);
	var sender = document.createTextNode(newMes.user+ ':  ');
	pr.appendChild(sender);
	var textMessage = document.createTextNode(newMes.text);
	mess.appendChild(textMessage);
	divItem.appendChild(pr);
	divItem.appendChild(mess);
	return divItem;
}

function updateHistory(newMessages) {
	for(var i = 0; i < newMessages.length; i++)
		addMessageInternal(newMessages[i]);
}

function addMessageInternal(message) {
	var historyBox = document.getElementById('messages2');
	var history = appState.history;

	history.push(message);
	var item = createMessage(message);
	historyBox.appendChild(item);
}

function doPolling() {
	function loop() {
		var url = appState.mainUrl + '?token=' + appState.token;

		get(url, function(responseText) {
			var response = JSON.parse(responseText);

			appState.token = response.token;
			updateHistory(response.messages);
			
			setTimeout(loop, 1000);
		}, function(error) {
			defaultErrorHandler(error);
			setTimeout(loop, 1000);
		});
	}

	loop();
}

function defaultErrorHandler(message) {
	var status = document.getElementsByClassName('status')[0];
	status.innerText= 'ERROR:\n' + message +'\n';
	console.error(message);
}

function get(url, continueWith, continueWithError) {
	ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError) {
	ajax('POST', url, data, continueWith, continueWithError);	
}
function del(url,data,continueWith,continueWithError){
	ajax('DELETE', url, data, continueWith, continueWithError);
}
function put(url,data,continueWith,continueWithError){
	ajax('PUT', url, data, continueWith, continueWithError);
}

function isError(text) {
	if(text == "")
		return false;
	
	try {
		var obj = JSON.parse(text);
	} catch(ex) {
		return true;
	}

	return !!obj.error;
}

function ajax(method, url, data, continueWith, continueWithError) {
	var xhr = new XMLHttpRequest();

	continueWithError = continueWithError || defaultErrorHandler;
	xhr.open(method || 'GET', url, true);

	xhr.onload = function () {
		if (xhr.readyState !== 4)
			return;

		if(xhr.status != 200) {
			continueWithError('Error on the server side, response ' + xhr.status);
			return;
		}

		if(isError(xhr.responseText)) {
			continueWithError('Error on the server side, response ' + xhr.responseText);
			return;
		}

		continueWith(xhr.responseText);
	};    

	xhr.ontimeout = function () {
		ontinueWithError('Server timed out !');
	}

	xhr.onerror = function (e) {
		var errMsg = 'Server connection error ' + appState.mainUrl + '\n';

		continueWithError(errMsg);
	};

	xhr.send(data);
}

window.onerror = function(err) {
	defaultErrorHandler(err.toString());
}

/*function onSaveMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butSave')){
		var message = document.getElementById('newMessage');
		saveMessage(message.value);
		message.value = '';
    }

}

function saveMessage(newText){
	var divList = document.body.children[1];
    var list = divList.children[0];
    var i=0;
    var mes = 0;
    var text = document.getElementById('newMessage');
    var editId;
	for (i=list.children.length-1; i>=0;i--){
		if ((checkSelectMessage(list.children[i]))!=0){
	        mes = list.children[i];
	        editId = list.children[i].id;
	        mes.children[1].innerText = newText;
	        for (var k=0; k< mesList.length;k++)
				if (mesList[k].id == editId){
					mesList[k].description = newText;
				}
	        mes.classList.remove('select');
	        store (mesList);
	    	return;
    	}
	}
}

function onEditMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butEdit')){
		var divList = document.body.children[1];
        var list = divList.children[0];
        var i=0;
        var mes = 0;
        var text = document.getElementById('newMessage');
    	for (i=list.children.length-1; i>=0;i--){
			if ((checkSelectMessage(list.children[i]))!=0){
	            mes = list.children[i];
	            text.value = mes.children[1].innerText;
	        	return;
       		}
		}
    }
}


function onDeleteMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butDelete')){
		var i=0;
		var j=0;
        var divList = document.body.children[1];
        var list = divList.children[0];
        var size = list.children.length;
        var deleteId;
        while (j < list.children.length){
        	i=0;
	        while ((i < list.children.length) && (checkSelectMessage(list.children[i]))==0) 
	        	i++;
	        if (i==list.children.length)
	        	break;
			if ((checkSelectMessage(list.children[i]))!=0){
				j=i;
				deleteId = list.children[i].id;
				list.removeChild(list.children[j]);
				for (var k=0; k< mesList.length;k++)
					if (mesList[k].id == deleteId){
						mesList.splice(k,1);
					}
			}
    	}
    	store(mesList);
	}
}

function checkSelectMessage(message){
	
	if (message.classList.contains('select'))
			return message;
	else return 0;
}

function onChangeNameClick(evtObj) {
	if(evtObj.type === 'click' && evtObj.target.classList.contains('butSign')){
		var val = document.getElementById('newN');
	changeName(val.value);
	storeUserName();
	val.value = '';
	}
}

function onSelectMessageClick(evtObj){
	if(evtObj.type === 'click' && evtObj.target.nodeName=='SPAN'){
		var mes = evtObj.target.parentElement;
		if(mes.classList.contains('select')) {
			mes.classList.remove('select');
		}
		else {
			mes.classList.add('select');
		}
	}
}


function addMessage(newMes) {
	var item = createMessage(newMes);
	var items = document.getElementsByClassName('box')[0];
	mesList.push(newMes);
	items.appendChild(item);
}

function createMessage(newMes){
	var divItem = document.createElement('div');
	divItem.setAttribute('id', newMes.id);
	var pr = document.createElement('span');
	var mess = document.createElement('span');
	var time = document.createTextNode (newMes.time + '  ');
	pr.appendChild (time);
	var sender = document.createTextNode(newMes.sender+ ':  ');
	pr.appendChild(sender);
	var textMessage = document.createTextNode(newMes.description);
	mess.appendChild(textMessage);
	divItem.appendChild(pr);
	divItem.appendChild(mess);
	return divItem;
}

function store(listToSave) {

	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	localStorage.setItem("Message List", JSON.stringify(listToSave));
}

function restore() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	var item = localStorage.getItem("Message List");

	return item && JSON.parse(item);
}
function changeName(value) {
	if((!value)||(value=='User Name')){
		return;
	}
	var item = document.getElementById('Name');
	item.innerText = value;
	storeUserName();
}
*/

