function getInvalidFields()
{
    var invalids = $(".required").filter(function() {
        return this.value == "";
    });

    //controllo approvazione condizioni
    var chk = $('input[type="checkbox"].required');
    if (chk.attr("checked") != "checked") {
        invalids = invalids.add(chk);
    }

    //controllo email
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if (!re.test($('#mail').val())) {
        invalids = invalids.add($('#mail'));
    }
    return invalids;
}

$(function() {
    $("#birthdate").datepicker({"autoSize": true, dateFormat: "dd/mm/yy"});
    $('#submit').click(function(e) {
        var invalids = getInvalidFields();
        if (invalids.length > 0) {
            $(invalids[0]).focus();
            invalids.addClass('invalid');
            alert("The form contains some missing or invalid data.")
            e.preventDefault();
        }
        
    });
    $('input').change(function() {
        $(this).removeClass('invalid')
    });
});