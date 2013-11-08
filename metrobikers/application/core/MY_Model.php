<?php

class MY_Model extends CI_Model {

    public function __construct() {
        $this->load->database();
    }

    protected function assign($object) {
        $ar = is_array($object) ? $object : get_object_vars($object);
        foreach ($this as $key => $value) {
            if (property_exists($ar, $key))
                $this->$key = $ar[$key];
        }
    }

}