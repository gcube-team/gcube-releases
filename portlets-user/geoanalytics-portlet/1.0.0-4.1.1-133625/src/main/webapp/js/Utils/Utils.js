function timestampToDateString(timestamp)
{
	var date = new Date(timestamp);
    
	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
	var dateStr = addZero(date.getMonth()+1) + '/' + addZero(date.getDate()) + "/" + date.getFullYear();
	return dateStr;
}

function timestampToDateStringDots(timestamp)
{
	var date = new Date(timestamp);
    
	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
	var dateStr = addZero(date.getMonth()+1) + '.' + addZero(date.getDate()) + "." + date.getFullYear();
	return dateStr;
}