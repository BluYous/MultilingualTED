import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../../common";
import {message, Select, Spin} from 'antd';
import {PubSub} from 'pubsub-js';

class EventFilter extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isLoaded: false,
            events: [],
        }
    }
    
    handleChange = (value) => {
        PubSub.publish('eventFilter', value);
    };
    
    handleFocus = (value) => {
        let {isLoaded} = this.state;
        if (!isLoaded) {
            axios.get(ajaxAddress + "/events")
                .then(res => {
                    let events = res.data;
                    this.setState({
                        isLoaded: true,
                        events,
                    })
                })
                .catch(e => {
                    message.error('Fail to load events');
                });
        }
    };
    
    render() {
        let {events, isLoaded} = this.state;
        return (
            <Select
                showSearch
                allowClear={true}
                style={{width: '100%'}}
                placeholder="Events"
                optionFilterProp="children"
                notFoundContent={!isLoaded ? <Spin size="default"/> : null}
                onChange={this.handleChange}
                onFocus={this.handleFocus}
            >
                {
                    events.map((item, index) => {
                        return (
                            <Select.Option
                                key={item.event_label}
                                title={`${item.count} talk(s)`}
                            >
                                {item.event_label}
                            </Select.Option>
                        )
                    })
                }
            </Select>
        )
    }
}

export default EventFilter;