function tableUtils(){
	
}

function startPreloader(){
	$("#element").introLoader();
}

function stopPreloader(){
	$("#element").data('introLoader').stop();
}

function currentUsersObjectForDataTable(
		CheckBox, UserName,
		Email, FullName,
		Roles, Teams, UserId,
		RequestDate, ValidationDate,
		reqID, AcceptanceAdmin,
		isSelf){
	this.CheckBox = CheckBox;
	this.UserName = UserName;
	this.Email = Email;
	this.FullName = FullName;
	this.Roles = Roles;
	this.Teams = Teams;
	this.UserId = UserId;
	this.RequestDate = returnDateInProperForm(RequestDate);
	this.ValidationDate = returnDateInProperForm(ValidationDate);
	this.reqID = reqID;
	this.AcceptanceAdmin = AcceptanceAdmin;
	this.isSelf = isSelf;
}

function usersRequestObjectForDataTable(
		CheckBox, UserName,
		Email, FullName,
		Message, UserId,
		RequestId, RequestDate){
	this.CheckBox = CheckBox;
	this.UserName = UserName;
	this.Email = Email;
	this.FullName = FullName;
	this.Message = Message;
	this.UserId = UserId;
	this.RequestDate = returnDateInProperForm(RequestDate);
	this.RequestId = RequestId;
}

function rejectedUsersRequestObjectForDataTable(
		CheckBox, UserName,
		Email, FullName,
		Message, UserId,
		RequestId, RequestDate, RejectionDate){
	this.CheckBox = CheckBox;
	this.UserName = UserName;
	this.Email = Email;
	this.FullName = FullName;
	this.Message = Message;
	this.UserId = UserId;
	this.RequestId = RequestId;
	this.RequestDate = returnDateInProperForm(RequestDate);
	this.RejectionDate = returnDateInProperForm(RejectionDate);
}

function siteTeamsObjectForDataTable(
		CheckBox, Name, TeamID,
		Description, NumberOfUsers,
		CreationDate, LastModificationDate,
		CreatorName, siteTeamUsers){
	this.CheckBox = CheckBox;
	this.Name = Name;
	this.TeamID = TeamID;
	this.Description = Description;
	this.NumberOfUsers = NumberOfUsers;
	this.CreationDate = /*returnDateInProperForm(*/CreationDate;
	this.LastModificationDate = /*returnDateInProperForm(*/LastModificationDate;
	this.CreatorName = CreatorName;
	this.siteTeamUsers = siteTeamUsers;
}

function siteTeamsUserObjectForDataTable(
		FullName,UserName){
	
	this.FullName = FullName;
	this.UserName = UserName;
}

function returnDateInProperForm(ValidationDate){
	if(ValidationDate !== null && ValidationDate !== undefined && ValidationDate !== '-'){
		var serverDate = new Date(Date.parse(ValidationDate));
		var properDate;
		var month = serverDate.getMonth()+1;
		month = (month.toString().length === 1) ? '0'+month : month;
		var day = serverDate.getDate().toString();
		day = (day.length === 1) ? '0'+day : day;
		var year = serverDate.getFullYear();
		properDate = month + '/' + day + '/' + year;		
		
		return properDate;
	}else return '-';
}

function formatSiteTeamUsers(array){
	var newArray = [];
	for(var i=0; i<array.length;i++){
		if(!$.isEmptyObject(array[i]) && array[i] !== null){
			array[i] = "<p>" + array[i].fullName + "</p>" + ",<p>" + array[i].screenName + "</p>";
			newArray.push(array[i]);
		}
	}
	
	return newArray;
}