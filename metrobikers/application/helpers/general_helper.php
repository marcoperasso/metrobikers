<?php

// any_in_array() is not in the Array Helper, so it defines a new function
function my_base_url() {
    return base_url() . "index.php/";
}

function set_user($user) {
    if (isset($_SESSION))
        $_SESSION["user"] = serialize($user);
}

function get_user() {
    $CI = & get_instance();
    $CI->load->model('User_model');
    return (isset($_SESSION) && isset($_SESSION['user'])) ? unserialize($_SESSION["user"]) : NULL;
}

?>
