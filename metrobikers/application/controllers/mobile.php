<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Mobile extends MY_Controller {

    public function version() {

        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode(array('version' => 1)));
    }

    public function user() {
        $user = get_user();
        $response = array();
        if ($user!=NULL)
        {
            $response = array(
                'logged' => TRUE,
                'name' => $user->name,
                'surname' => $user->surname,
                'birthdate' => $user->birthdate,
                );
        }
        else
        {
            $response = array(
                'logged' => FALSE
                );
        }
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode($response));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */