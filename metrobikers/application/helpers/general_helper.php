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
        case 1:
            return "Femmina";
        case 2:
            return "Maschio";

        default:

            return "Non specificato";
    }
}

function htmlSpaceIfEmpty($string)
{
    return empty($string) ? '&nbsp;' : $string;
}
?>
