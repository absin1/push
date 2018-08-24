console.log('Hello');
function simple() {
	if (!!window.EventSource) {
		console.log('Event Source works!');
		var source = new EventSource('h2push');
		source.addEventListener('message', function(e) {
			console.log(e.data);
		}, false);

		source.addEventListener('open', function(e) {
			console.log(e);
			// Connection was opened.
		}, false);

		source.addEventListener('error', function(e) {
			if (e.readyState == EventSource.CLOSED) {
				console.log(e);
				// Connection was closed.
			}
		}, false);
	} else {
		// Result to xhr polling :(
		console.log('Error');
	}
}
function echo() {
	var output = document.getElementById("output");
	if (typeof (EventSource) !== "undefined") {
		var msg = document.getElementById("textID").value;
		var source = new EventSource("simplesse?user=" + msg);
		source.onmessage = function(event) {
			output.innerHTML += event.data + "<br>";
		};
	} else {
		output.innerHTML = "Sorry, Server-Sent Event is not supported in your browser";
	}
	return false;
}
function asynch() {
	var output = document.getElementById("output");
	if (typeof (EventSource) !== "undefined") {
		var msg = document.getElementById("textID").value;
		var source = new EventSource("sseasync?msg=" + msg);
		source.onmessage = function(event) {
			output.innerHTML += event.data + "<br>";
		};
		source.addEventListener('close', function(event) {
			output.innerHTML += event.data + "<hr/>";
			source.close();
		}, false);
	} else {
		output.innerHTML = "Sorry, Server-Sent Events are not supported in your browser";
	}
	return false;
}