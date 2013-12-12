<?php

function set_user($user) {
    if (isset($_SESSION))
        $_SESSION["user"] = serialize($user);
}

function get_user() {
    $CI = & get_instance();
    $CI->load->model('User_model');
    return (isset($_SESSION) && isset($_SESSION['user'])) ? unserialize($_SESSION["user"]) : NULL;
}

function decode_gender($genderId) {
    switch ($genderId) {
        case GENDER_FEMALE:
            return "Femmina";
        case GENDER_MALE:
            return "Maschio";
        default:
            return "Non specificato";
    }
}

function decode_position_policy($id) {
    switch ($id) {
        case POSITION_POLICY_ALL_NONAME:
            return "Chiunque, ma non visualizzare il mio nome";
        case POSITION_POLICY_GROUP:
            return "Gli appartenenti al mio gruppo";
        case POSITION_POLICY_GROUP_NONAME:
            return "Gli appartenenti al mio gruppo, ma non visualizzare il mio nome";
        default:
            return "Chiunque";
    }
}

function position_policy_items() {
    return "['" . 
            decode_position_policy(POSITION_POLICY_ALL) . "', '" . 
            decode_position_policy(POSITION_POLICY_ALL_NONAME) . "', '" . 
            decode_position_policy(POSITION_POLICY_GROUP) . "', '" . 
            decode_position_policy(POSITION_POLICY_GROUP_NONAME) . "']";
}

function gender_items() {
    return "['" . 
            decode_gender(GENDER_UNSPECIFIED) . "', '" . 
            decode_gender(GENDER_FEMALE) . "', '" . 
            decode_gender(GENDER_MALE) . "']";
}

function htmlSpaceIfEmpty($string) {
    return empty($string) ? '&nbsp;' : html_escape($string);
}

function adjust_post($content) {
    return str_replace(array('\r\n', '\n'), array('<br>', '<br>'), html_escape($content));
}

?>
