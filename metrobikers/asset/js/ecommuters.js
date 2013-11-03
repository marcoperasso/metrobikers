/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(function() {
    setContentHeight();
    $(window).resize(setContentHeight);
})
function setContentHeight()
{
    $("body").css("min-height", ($(window).height()) + "px");
}
