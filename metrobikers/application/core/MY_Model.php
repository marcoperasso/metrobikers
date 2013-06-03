<?php

class MY_Model extends CI_Model {

    public function __construct() {
        $this->load->database();
    }

    protected function assign($object) {
        foreach (get_object_vars($object) as $key => $value) {
            $this->$key = $value;
        }
    }

}