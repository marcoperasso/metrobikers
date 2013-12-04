/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(function() {
    setContentHeight();
    $(window).resize(setContentHeight);
    var idx = 50;
    $(".changeable").each(function() {
        this.tabIndex = idx++;
        attachControl(this);
    });

    $('.modal').on('show.bs.modal', function() {
        $('.autofocus', this).focus();
    }).keypress(function(e) {
        var jObj = $(this);
        if (e.which === 13 && jObj.is(":visible")) {
           jObj.find('.btn-default').click();
        }
    });
    
    $('.autofocus').focus();
});
function setContentHeight()
{
    $("body").css("min-height", ($(window).height()) + "px");
}

function setActiveTab(tabClass)
{
    $('li.' + tabClass).addClass('active');
}
