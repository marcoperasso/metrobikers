<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class User extends MY_Controller {

    /**
     * Index Page for this controller.
     *
     * Maps to the following URL
     * 		http://example.com/index.php/welcome
     * 	- or -  
     * 		http://example.com/index.php/welcome/index
     * 	- or -
     * Since this controller is set as the default controller in 
     * config/routes.php, it's displayed at http://example.com/
     *
     * So any other public methods not prefixed with an underscore will
     * map to /index.php/welcome/<method_name>
     * @see http://codeigniter.com/user_guide/general/urls.html
     */
    public function index() {

        $data = array();
        $user = get_user();
        if ($user != NULL) {
            $data['user'] = $user;
        }

        $this->load_view('user', "I miei dati",  $data);
    }
    
    public function routes() {

        $data = array();
        $user = get_user();
        if ($user != NULL) {
            $data['user'] = $user;

            $this->load->model("Route_model");
            $this->load->model("Route_points_model");
            $this->Route_model->userid = $user->id;
            $routes = $this->Route_model->get_routes('0000-00-00 00:00:00');
            foreach ($routes as $route) {
                $this->Route_points_model->routeid = $route->id;
                $route->points = $this->Route_points_model->get_points();
            }
            $data['routes'] = $routes;
        }

        $this->load_view('routes', 'I miei itinerari', $data);
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
