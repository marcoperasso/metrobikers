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

function decode_showposition($id) {
    switch ($id) {
        case SHOW_POSITION_ALL:
            return "chiunque";
        case SHOW_POSITION_GROUP:
            return "gli appartenenti al mio gruppo";
        case SHOW_POSITION_NONE:
            return "nessuno";
        default:
            return "chiunque";
    }
}

function decode_showname($id) {
    switch ($id) {
        case SHOW_NAME_ALL:
            return "chiunque";
        case SHOW_NAME_GROUP:
            return "gli appartenenti al mio gruppo";
        case SHOW_NAME_NONE:
            return "nessuno";
        default:
            return "chiunque";
    }
}

function showposition_items() {
    return "['" .
            decode_showposition(SHOW_POSITION_ALL) . "', '" .
            decode_showposition(SHOW_POSITION_GROUP) . "', '" .
            decode_showposition(SHOW_POSITION_NONE) . "']";
}

function showname_items() {
    return "['" .
            decode_showname(SHOW_NAME_ALL) . "', '" .
            decode_showname(SHOW_NAME_GROUP) . "', '" .
            decode_showname(SHOW_NAME_NONE) . "']";
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

function get_user_photo($userid) {
    if (file_exists('./user_data/' . $userid . '/photo.png'))
        return '/user_data/' . $userid . '/photo.png';
    
    if (file_exists('./user_data/' . $userid . '/photo.jpg'))
        return '/user_data/' . $userid . '/photo.jpg';
    
    
    return "/asset/img/ecommuter.png";
}

?>
