import React, {Component} from 'react';
import {Select} from 'antd';
import {PubSub} from 'pubsub-js';

class SortFilter extends Component {
    handleChange = (value) => {
        PubSub.publish('sortFilter', value);
    };
    
    render() {
        return (
            <Select
                style={{width: '100%'}}
                onChange={this.handleChange}
                defaultValue={this.props.defaultValue}
            >
                <Select.Option value={1} title='Sort by'>Newest</Select.Option>
                <Select.Option value={2} title='Sort by'>Oldest</Select.Option>
                <Select.Option value={3} title='Sort by'>Most Viewed</Select.Option>
            </Select>
        )
    }
}

export default SortFilter;