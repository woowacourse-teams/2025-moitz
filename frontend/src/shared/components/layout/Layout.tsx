import * as layout from './layout.styled';

interface LayoutProps {
  children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
  return (
    <div css={layout.container()}>
      <div css={layout.side()}></div>
      <div css={layout.content()}>{children}</div>
      <div css={layout.side()}></div>
    </div>
  );
}

export default Layout;
