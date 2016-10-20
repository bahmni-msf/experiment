function logRole() {
  var spreadSheet = SpreadsheetApp.getActiveSpreadsheet();
  
  var sqlSheet = spreadSheet.getSheetByName("SQL");
  var rolesSheet = spreadSheet.getSheetByName("roles");
  
  sqlSheet.clear();
  
  sqlSheet.appendRow(["insert into privilege values ('app:clinical:treatmentTab', 'View Treatment tab', UUID());"]);
  sqlSheet.appendRow(["insert into privilege values ('app:clinical:ordersTab', 'View Orders tab', UUID());"]);
  sqlSheet.appendRow(["insert into privilege values ('app:clinical:bacteriologyTab', 'View Bacteriology tab', UUID());"]);  
  sqlSheet.appendRow(["insert into privilege values ('app:implementer-interface', 'Will give access to implementer interface app', UUID());"]);  
  sqlSheet.appendRow(["insert into privilege values ('app:radiology-upload', 'Will give access to radiology app', UUID());"]);  
  sqlSheet.appendRow(["insert into privilege values ('app:patient-documents', 'Will give access to patient documents app', UUID());"]);  

  var data = rolesSheet.getDataRange().getValues();
  
  for (var col = 0; col < data[0].length; col++) {
    var state = 1;
    var skip = false;
    sqlSheet.appendRow([" "]);
    for (var row = 1; row < data.length; row++) {
      Logger.log(row + ' ' +  col + ' ' + data[row][col]);
      var elem = data[row][col];
      elem = elem.trim();
      switch(state) {
        case 1:
          if (elem === '') {
            skip = true;
            continue;
          }
          if (skip === true && elem !== '') {
            row--;
            state = 2;
            skip = false;
            continue;
          }
          sqlSheet.appendRow([Utilities.formatString("# Create %s role", data[1][col])]);
          sqlSheet.appendRow([Utilities.formatString("insert into role values('%s', '%s', UUID());", data[1][col], data[0][col])]);
          break;
        case 2:
          if (elem === '') {
            skip = true;
            continue;
          }
          if (skip === true && elem !== '') {
            row--;
            state = 3;
            skip = false;
            continue;
          }
          if (elem !== 'DUMMY') {
            sqlSheet.appendRow([Utilities.formatString("insert into role_role values('%s', '%s');", elem, data[1][col])]);
          }
          break;
        case 3:
          if (elem === '') {
            skip = true;
            continue;
          }
          if (skip === true && elem !== '') {
            row--;
            skip = false;
            continue;
          }
          sqlSheet.appendRow([Utilities.formatString("insert into role_privilege values('%s', '%s');", data[1][col], elem)]);
          break;
      }
    }
  }
  
  sqlSheet.appendRow([""]);
  sqlSheet.appendRow(["# Create SuperAdmin role"])
  sqlSheet.appendRow(["insert into role values('SuperAdmin', 'Will give full acess to Bahmni and OpenMRS', UUID());"]);
  sqlSheet.appendRow(["insert into role_privilege select 'SuperAdmin',privilege from privilege;"]);
}
