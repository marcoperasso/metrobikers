<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Test extends MY_Controller {

    
    public function index() {
        echo my_base_url();
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */