<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Home extends MY_Controller {


    public function index() {
        
        $this->load_view('home', "Benvenuto in ECOmmuters");
    }
    
    public function mission() {
        $this->load_view('mission');
    }

  
}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */