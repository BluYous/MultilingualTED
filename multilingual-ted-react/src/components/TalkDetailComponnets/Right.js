import React, {Component} from 'react';
import {Affix, Col, List, Row} from 'antd';

class Right extends Component {
    render() {
        let {talk} = this.props;
        return (
            <div>
                <Affix offsetTop={100}>
                    <Row type="flex" justify="center">
                        <Col span={24}>
                            <List
                                size="small"
                                header={<b>Download</b>}
                                bordered
                                dataSource={talk.downloads}
                                renderItem={download => (
                                    <List.Item>
                                        <a href={download.uri}>{download.download_type} ({(download.file_size_bytes / 1024 / 1024).toFixed(2)}MB)</a>
                                    </List.Item>
                                )}
                            />
                        </Col>
                    </Row>
                </Affix>
            </div>
        )
    }
}

export default Right;