import jsxobj from 'jsxobj';

const CustomPlugin = (config) => ({
  ...config,
  name: 'custom-plugin',
});

export default (
  <webpack target="web" watch mode="development">
    <entry path="src/index.js" />
    <resolve>
      <alias
        {...{
          react: 'preact-compat',
          'react-dom': 'preact-compat',
        }}
      />
    </resolve>
    <plugins>
      <CustomPlugin foo="bar" />
    </plugins>
  </webpack>
);
