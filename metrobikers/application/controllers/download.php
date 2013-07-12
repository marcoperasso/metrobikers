<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Download extends MY_Controller {

    public function __construct() {
        parent::__construct();
    }

    public function gpx($route_name) {
        $user = get_user();
        if ($user == NULL)
            return;
        $this->load->model("Route_model");
        $this->load->model("Route_points_model");
        $this->Route_model->name = $route_name;
        $this->Route_model->userid = $user->id;
        if (!$this->Route_model->get_route())
            return;
        $this->Route_points_model->routeid = $this->Route_model->id;
        $points = $this->Route_points_model->get_points();
        $this->load->helper('xml');
        
        $xml =
                '<?xml version="1.0" encoding="UTF-8" standalone="no" ?>
<gpx xmlns="http://www.topografix.com/GPX/1/1" creator="ECOmmuters" version="1.1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd">
  <metadata>
    <link href="http://www.ecommuters.com">
      <text>ECOmmuters TEAM, SL</text>
    </link>
    
  </metadata>
  <trk>
    <name>' . xml_convert($this->Route_model->name) . '</name>
    <trkseg>
      ';

        foreach ($points as $point) {
            $xml .='<trkpt lat="' . $point->lat/1000000 . '" lon="' . $point->lon/1000000 . '">
        <ele>' . $point->ele . '</ele>
        <time>' . $point->time . '</time>
      </trkpt>';
        };
        $xml .=
                '</trkseg>
  </trk>
</gpx>';
        header('Content-Type: text/xml;charset=UTF-8');
        //header("Content-Disposition", "attachment;filename=a.gpx");
        echo $xml;
    }

}

/* End of file welcome.php */
    /* Location: ./application/controllers/welcome.php */