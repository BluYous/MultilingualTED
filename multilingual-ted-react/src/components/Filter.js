import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../common";
import {Row, Col} from 'antd';
import TopicFilter from "./FilterSubComponents/TopicFilter";

class Filter extends Component {
    constructor(props) {
        super(props);
    }
    
    componentWillMount() {
    }
    
    render() {
        return (
            <div>
                <Row>
                    <Col span={8}><TopicFilter/></Col>
                </Row>
            </div>
        )
    }
}

export default Filter;