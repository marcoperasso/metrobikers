<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Home extends MY_Controller {


    public function index() {
        
        $this->load_view('home');
    }
    
    public function mission() {
        $this->load_view('mission');
    }

  public function details() {
        $this->load_view('details');
    }
}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */