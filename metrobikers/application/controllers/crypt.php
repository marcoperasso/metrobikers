<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Crypt extends MY_Controller {

   

    public function __construct() {
        parent::__construct();
    }

    public function index() {
        header('Content-Type: text/plain;charset=UTF-8');
        $this->load->library('Crypter');
        $s = $this->crypter->get_script();
        echo $s;
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */